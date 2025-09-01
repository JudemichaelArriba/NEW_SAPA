<?php
include "db_config.php";
header('Content-Type: application/json');


$username = $_POST['username'] ?? '';
$password = $_POST['password'] ?? '';


if (!$username || !$password) {
    echo json_encode(["status" => "failed", "message" => "Missing fields"]);
    exit;
}


$stmt = $conn->prepare("CALL loginUser(?)");
$stmt->bind_param("s", $username);
$stmt->execute();

$result = $stmt->get_result();


if ($result && $result->num_rows > 0) {
    $user = $result->fetch_assoc();

    
    if (password_verify($password, $user['password'])) {
        echo json_encode([
            "status" => "success",
            "user_id" => $user['user_id'],
            "request_status" => $user['request_status'] ?? null
        ]);
    } else {
        echo json_encode(["status" => "failed", "message" => "Invalid credentials"]);
    }
} else {
    echo json_encode(["status" => "failed", "message" => "User not found"]);
}


$stmt->close();
$conn->close();
?>
