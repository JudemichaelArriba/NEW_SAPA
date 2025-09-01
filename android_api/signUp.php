<?php

ini_set('display_errors', 0);
error_reporting(0);

header('Content-Type: application/json');


include 'db_config.php';

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $first_name = $_POST["first_name"] ?? '';
    $last_name = $_POST["last_name"] ?? '';
    $email = $_POST["email"] ?? '';
    $contact_no = $_POST["contact_no"] ?? '';
    $username = $_POST["username"] ?? '';
    $password = $_POST["password"] ?? '';


    if (!$first_name || !$last_name || !$email || !$contact_no || !$username || !$password) {
        echo json_encode(["success" => false, "message" => "Missing fields"]);
        exit;
    }

    $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    $stmt = $conn->prepare("CALL add_user(?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("ssssss", $first_name, $last_name, $email, $contact_no, $username, $hashed_password);

    try {
        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "User added successfully."]);
        } else {
      
            $sqlMessage = $stmt->error ?: $conn->error;
            echo json_encode(["success" => false, "message" => $sqlMessage]);
        }
    } catch (mysqli_sql_exception $e) {

        echo json_encode(["success" => false, "message" => $e->getMessage()]);
    }

    $stmt->close();
    $conn->close();
}
?>
