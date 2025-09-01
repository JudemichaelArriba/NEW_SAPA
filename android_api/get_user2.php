<?php
header("Content-Type: application/json");
require_once 'db_config.php';

$user_id = $_GET['user_id'] ?? '';

if (empty($user_id)) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing user_id"
    ]);
    exit;
}

$sql = "CALL getUserProfile(?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $user_id);
$stmt->execute();

$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    $data = [
        'user_id'       => $row['user_id'],
        'full_name'     => $row['full_name'],
        'email'         => $row['email'],
        'mobile'        => $row['mobile'],
        'username'      => $row['username'],
        'role'          => $row['role'],
        'status'        => $row['status'],
        'profile_image' => !empty($row['profile_image'])
                            ? 'data:image/jpeg;base64,' . base64_encode($row['profile_image'])
                            : null
    ];

    echo json_encode([
        "status" => "success",
        "data" => $data
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "User not found"
    ]);
}

$stmt->close();
$conn->close();
?>
