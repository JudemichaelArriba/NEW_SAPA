<?php
require 'db_config.php';

header('Content-Type: application/json');

$user_id       = $_POST['user_id'] ?? '';
$first_name    = $_POST['first_name'] ?? '';
$last_name     = $_POST['last_name'] ?? '';
$email         = $_POST['email'] ?? '';
$contact_no    = $_POST['contact_no'] ?? '';
$username      = $_POST['username'] ?? '';
$password      = $_POST['password'] ?? '';
$profile_image = $_POST['profile_image'] ?? '';

$hashed_password = !empty($password) ? password_hash($password, PASSWORD_DEFAULT) : null;
$profile_image_binary = !empty($profile_image) ? base64_decode($profile_image) : null;

$response = array();

try {
    $stmt = $conn->prepare("CALL update_user(?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param(
        "ssssssss",
        $user_id,
        $first_name,
        $last_name,
        $email,
        $contact_no,
        $username,
        $hashed_password,
        $profile_image_binary
    );

    $stmt->execute();

    $response['status'] = 'success';
    $response['message'] = 'Profile updated successfully.';

    $stmt->close();
} catch (mysqli_sql_exception $e) {

    $response['status'] = 'error';
    $response['message'] = $e->getMessage();
}

$conn->close();

echo json_encode($response);
?>
