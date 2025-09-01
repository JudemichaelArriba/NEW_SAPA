<?php
require 'db_config.php';
header('Content-Type: application/json');

$appointment_id = $_POST['appointment_id'] ?? '';

if (empty($appointment_id)) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing appointment_id."
    ]);
    exit;
}

try {
    $stmt = $conn->prepare("CALL cancel_appointment_by_id(?)");
    $stmt->bind_param("i", $appointment_id);
    $stmt->execute();
    $stmt->close();

  
    echo json_encode([
        "status" => "success",
        "message" => "Appointment with ID $appointment_id has been cancelled."
    ]);

} catch (mysqli_sql_exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>
