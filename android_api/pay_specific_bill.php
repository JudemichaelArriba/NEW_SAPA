<?php
require 'db_config.php';

header('Content-Type: application/json');

$user_id = $_POST['user_id'] ?? '';
$bill_code = $_POST['bill_code'] ?? '';

if (empty($user_id) || empty($bill_code)) {
    echo json_encode([
        "status" => "error",
        "message" => "User ID or Bill Code is missing."
    ]);
    exit;
}

try {
    $user_id_safe = $conn->real_escape_string($user_id);
    $bill_code_safe = $conn->real_escape_string($bill_code);

  
    $sql = "CALL pay_specific_bill('$bill_code_safe', '$user_id_safe')";
    $conn->query($sql);

    echo json_encode([
        "status" => "success",
        "message" => "Bill successfully paid."
    ]);

} catch (mysqli_sql_exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>
