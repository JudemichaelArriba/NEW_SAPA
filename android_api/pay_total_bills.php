<?php
require 'db_config.php';

header('Content-Type: application/json');

$user_id = $_POST['user_id'] ?? '';
$amount = isset($_POST['amount']) ? (float)$_POST['amount'] : 0.00;

if (empty($user_id) || $amount <= 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid user ID or amount."
    ]);
    exit;
}

try {

    $user_id_safe = $conn->real_escape_string($user_id);
    
  
    $sql = "CALL pay_total_bills('$user_id_safe', $amount)";
    $conn->query($sql);

    echo json_encode([
        "status" => "success",
        "message" => "Payment processed successfully."
    ]);

} catch (mysqli_sql_exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>
