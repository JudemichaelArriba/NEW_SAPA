<?php
header('Content-Type: application/json');
require 'db_config.php';

$first_name     = $_POST['first_name'] ?? '';
$last_name      = $_POST['last_name'] ?? '';
$contact_no     = $_POST['contact_no'] ?? '';
$email          = $_POST['email'] ?? '';
$birthdate      = $_POST['birthdate'] ?? '';
$gender         = $_POST['gender'] ?? '';
$coordinator_id = $_POST['coordinator_id'] ?? '';
$school_id      = $_POST['school_id'] ?? '';

$response = [];


mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $stmt = $conn->prepare("CALL add_student(?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param(
        "ssssssss", 
        $first_name, 
        $last_name, 
        $contact_no, 
        $email, 
        $birthdate, 
        $gender, 
        $coordinator_id, 
        $school_id
    );

    $stmt->execute();

    $response = ["error" => false, "message" => "Student added successfully"];

} catch (mysqli_sql_exception $e) {
   
    $response = ["error" => true, "message" => $e->getMessage()];
}

echo json_encode($response);

if (isset($stmt)) $stmt->close();
$conn->close();
