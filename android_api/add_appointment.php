<?php
error_reporting(E_ALL);
ini_set('display_errors', 0); 
ini_set('log_errors', 1);
ini_set('error_log', __DIR__ . '/php_error.log'); 

header('Content-Type: application/json');

require_once 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
    exit;
}

$data = json_decode(file_get_contents('php://input'), true);

$slot_id     = $data['slot_id'] ?? null;
$user_id     = $data['user_id'] ?? null;
$student_ids = $data['student_ids'] ?? [];

if (!$slot_id || !$user_id || !is_array($student_ids) || empty($student_ids)) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid input']);
    exit;
}

$student_ids_str = implode(',', $student_ids);

try {
    $stmt = $conn->prepare("CALL add_appointment_multiple_students(?, ?, ?)");
    if (!$stmt) {
        error_log("Prepare failed: " . $conn->error);
        echo json_encode(['status' => 'error', 'message' => 'Database prepare failed']);
        exit;
    }

    $stmt->bind_param("iss", $slot_id, $user_id, $student_ids_str);

    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => 'Appointment added successfully']);
    } else {
        error_log("Execute failed: " . $stmt->error);
        echo json_encode(['status' => 'error', 'message' => 'Failed to add appointment']);
    }

    $stmt->close();
} catch (Exception $e) {
    error_log("Exception: " . $e->getMessage());
    echo json_encode(['status' => 'error', 'message' => 'Exception occurred']);
}

$conn->close();
