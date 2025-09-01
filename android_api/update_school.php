<?php
require 'db_config.php';

header('Content-Type: application/json');

$school_id      = $_POST['school_id'] ?? '';
$school_name    = $_POST['school_name'] ?? '';
$school_address = $_POST['school_address'] ?? '';
$school_email   = $_POST['school_email'] ?? '';
$contact_no     = $_POST['contact_no'] ?? '';
$profile_image  = $_POST['profile_image'] ?? '';

$profile_image_binary = !empty($profile_image) ? base64_decode($profile_image) : null;

$response = array();

try {
    $stmt = $conn->prepare("CALL update_school(?, ?, ?, ?, ?, ?)");
    $stmt->bind_param(
        "sssssb",
        $school_id,
        $school_name,
        $school_address,
        $school_email,
        $contact_no,
        $profile_image_binary
    );

    $stmt->send_long_data(5, $profile_image_binary);

    $stmt->execute();

    $response['status'] = 'success';
    $response['message'] = 'School updated successfully.';

    $stmt->close();
} catch (mysqli_sql_exception $e) {
    $response['status'] = 'error';
    $response['message'] = $e->getMessage();
}

$conn->close();

echo json_encode($response);
?>
