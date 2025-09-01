<?php
require 'db_config.php';

header('Content-Type: application/json');

$student_id  = $_POST['student_id'] ?? '';
$first_name  = $_POST['first_name'] ?? '';
$last_name   = $_POST['last_name'] ?? '';
$contact_no  = $_POST['contact_no'] ?? '';
$email       = $_POST['email'] ?? '';
$birthdate   = $_POST['birthdate'] ?? ''; 
$gender      = $_POST['gender'] ?? '';

$response = array();

try {
    $stmt = $conn->prepare("CALL update_student(?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param(
        "sssssss",
        $student_id,
        $first_name,
        $last_name,
        $contact_no,
        $email,
        $birthdate,
        $gender
    );

    $stmt->execute();

    $response['status'] = 'success';
    $response['message'] = 'Student updated successfully.';

    $stmt->close();
} catch (mysqli_sql_exception $e) {
    $response['status'] = 'error';
    $response['message'] = $e->getMessage();
}

$conn->close();

echo json_encode($response);
?>
