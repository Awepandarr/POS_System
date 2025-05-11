
from flask import Flask, Response, render_template, jsonify, request
import cv2
from pyzbar.pyzbar import decode
import threading
import requests
import numpy
import atexit
import time
import logging
from flask_cors import CORS

# Configure logging
logging.basicConfig(level=logging.DEBUG, 
                    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

# Configuration with precise positioning
config = {
    'java_service_url': "http://localhost:8080/product",
    'camera_index': 1,
    'camera_width': 640,
    'camera_height': 480,
    
    # Precisely positioned rectangle
    'scan_rect': {
        'x': 240,     # Start 80 pixels from left
        'y': 290,    # Start 120 pixels from top
        'width':  1430,  # 3/4 of frame width (640 * 0.75)
        'height': 500  # 1/2 of frame height (480 * 0.5)
    },
    
    'request_timeout': 5,
    'scan_delay': 2,
    'polling_delay': 0.03
}

# Initialize the camera
def init_camera():
    camera = cv2.VideoCapture(config['camera_index'])
    camera.set(cv2.CAP_PROP_FRAME_WIDTH, config['camera_width'])
    camera.set(cv2.CAP_PROP_FRAME_HEIGHT, config['camera_height'])
    
    if not camera.isOpened():
        logger.error("Error: Could not access the camera.")
        alt_index = 0 if config['camera_index'] == 1 else 1
        logger.info(f"Trying alternative camera index: {alt_index}")
        
        camera = cv2.VideoCapture(alt_index)
        if not camera.isOpened():
            logger.error("Error: Could not access any camera.")
            return None
            
    logger.info("Camera initialized successfully.")
    return camera

cap = init_camera()

# Global variables
output_frame = None
frame_lock = threading.Lock()
latest_barcode_data = None
latest_product_info = None

def fetch_product_info(barcode):
    """Fetch product information from the Java web service."""
    global latest_product_info
    
    try:
        response = requests.get(
            config['java_service_url'], 
            params={"barcode": barcode}, 
            timeout=config['request_timeout']
        )
        
        if response.status_code == 200:
            product_info = response.json()
            logger.info(f"Product information fetched: {product_info}")
            latest_product_info = product_info
            return product_info
        else:
            logger.error(f"Error: Could not fetch product data, status code: {response.status_code}")
            latest_product_info = None
            return None
    except Exception as e:
        logger.error(f"Exception during web service call: {e}")
        return None

def camera_loop():
    """
    Continuously capture frames, draw guiding rectangle,
    perform barcode scanning, and update the global output frame.
    """
    global output_frame, latest_barcode_data, cap
    
    if cap is None:
        logger.error("Camera not initialized. Cannot start camera loop.")
        return
        
    last_scan_time = 0
    
    # Extract rectangle configuration
    rect = config['scan_rect']
    guiding_box_start = (rect['x'], rect['y'])
    guiding_box_end = (rect['x'] + rect['width'], rect['y'] + rect['height'])

    # Extensive logging for diagnosis
    logger.debug(f"Frame Size: {config['camera_width']}x{config['camera_height']}")
    logger.debug(f"Rectangle Start: {guiding_box_start}")
    logger.debug(f"Rectangle End: {guiding_box_end}")
    logger.debug(f"Rectangle Size: {rect['width']}x{rect['height']}")

    while True:
        ret, frame = cap.read()
        if not ret:
            logger.warning("Failed to grab frame")
            time.sleep(1)
            cap = init_camera()
            if cap is None:
                time.sleep(5)
            continue

        # Draw rectangle EXACTLY where specified
        cv2.rectangle(frame, guiding_box_start, guiding_box_end, (0, 0, 255), 2)

        # Barcode scanning logic
        current_time = time.time()
        if current_time - last_scan_time > config['scan_delay']:
            try:
                # Scan only within specified rectangle
                roi = frame[rect['y']:rect['y']+rect['height'], 
                            rect['x']:rect['x']+rect['width']]
                barcodes = decode(roi)
                
                if barcodes:
                    barcode = barcodes[0]
                    barcode_data = barcode.data.decode("utf-8")
                    barcode_type = barcode.type
                    
                    if latest_barcode_data is None or latest_barcode_data["data"] != barcode_data:
                        logger.info(f"Detected barcode: {barcode_data} ({barcode_type})")
                        latest_barcode_data = {"data": barcode_data, "type": barcode_type}
                        
                        # Draw barcode polygon
                        points = barcode.polygon
                        if points:
                            hull = cv2.convexHull(
                                numpy.array([[p.x + rect['x'], p.y + rect['y']] for p in points])
                            )
                            cv2.polylines(frame, [hull], True, (0, 255, 0), 2)
                        
                        # Add barcode text
                        cv2.putText(
                            frame, barcode_data, 
                            (rect['x'], rect['y'] - 10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2
                        )
                        
                        threading.Thread(target=fetch_product_info, args=(barcode_data,)).start()
                        
                        last_scan_time = current_time
            except Exception as e:
                logger.error(f"Barcode processing error: {e}")

        # Update global frame
        with frame_lock:
            output_frame = frame.copy()

        time.sleep(config['polling_delay'])

# Rest of the Flask app remains the same...

# Rest of the code remains the same as in the previous script...

# Rest of the code remains the same as in the previous script...


# Start the camera loop in a separate daemon thread
if cap is not None:
    camera_thread = threading.Thread(target=camera_loop)
    camera_thread.daemon = True
    camera_thread.start()
    logger.info("Camera thread started")
else:
    logger.error("Failed to start camera thread - no camera available")

def generate_frames():
    """
    Generator function that encodes the latest frame as JPEG and yields it
    in a multipart response format suitable for streaming.
    """
    global output_frame
    while True:
        with frame_lock:
            if output_frame is None:
                continue
            ret, buffer = cv2.imencode('.jpg', output_frame)
            if not ret:
                continue
            frame_bytes = buffer.tobytes()

        # Yield frame in multipart format
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame_bytes + b'\r\n')

@app.route('/')
def index():
    """Render the main page that displays the video stream."""
    return render_template('http://localhost:5001/video_feed')  # Change 'index.html' to 'live.html'

@app.route('/video_feed')
def video_feed():
    """Route that provides the MJPEG stream."""
    return Response(generate_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')

@app.route('/latest_barcode', methods=['GET'])
def get_latest_barcode():
    """Return the most recently detected barcode data as JSON."""
    if latest_barcode_data:
        return jsonify(latest_barcode_data)
    else:
        logger.info("No barcode detected yet")
        return jsonify({"error": "No barcode detected yet"}), 404

@app.route('/latest_product', methods=['GET'])
def get_latest_product():
    """Return the most recently fetched product data as JSON."""
    if latest_product_info:
        return jsonify(latest_product_info)
    else:
        return jsonify({"error": "No product information available"}), 404

@app.route('/health', methods=['GET'])
def health_check():
    """Simple health-check endpoint."""
    if cap is not None and cap.isOpened():
        return jsonify({"status": "OK", "camera": "Connected"})
    else:
        return jsonify({"status": "Degraded", "camera": "Disconnected"}), 503

@app.route('/scan', methods=['POST'])
def manual_scan():
    """Endpoint to trigger a scan with a manually entered barcode."""
    data = request.json
    if not data or 'barcode' not in data:
        return jsonify({"error": "Missing barcode data"}), 400
        
    barcode = data['barcode']
    global latest_barcode_data
    latest_barcode_data = {"data": barcode, "type": "MANUAL"}
    
    # Fetch product info
    product_info = fetch_product_info(barcode)
    if product_info:
        return jsonify(product_info)
    else:
        return jsonify({"error": "Product not found"}), 404

def release_camera():
    """Release the camera and close any OpenCV windows when exiting."""
    global cap
    if cap is not None and cap.isOpened():
        cap.release()
    cv2.destroyAllWindows()
    logger.info("Camera resources released")

atexit.register(release_camera)

if __name__ == '__main__':
    try:
        port = 5001
        logger.info(f"Starting Flask server on port {port}...")
        app.run(host='0.0.0.0', port=port, debug=False, threaded=True)
    except Exception as e:
        logger.error(f"Error starting Flask server: {e}")