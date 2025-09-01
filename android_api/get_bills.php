<?php
error_reporting(0);
ini_set('display_errors', 0);
header('Content-Type: application/json');
include 'db_config.php'; 

$user_id = $_GET['user_id'] ?? null;

if (!$user_id) {
    echo json_encode(["error" => "Missing user_id"]);
    exit;
}

$total = 0;
$bills = [];

$sql = "CALL get_user_bills('$user_id')";

if ($conn->multi_query($sql)) {

    if ($result = $conn->store_result()) {
        while ($row = $result->fetch_assoc()) {
            $bills[] = $row;
        }
        $result->free();
    }

    if ($conn->more_results()) {
        $conn->next_result();
        if ($result_total = $conn->store_result()) {
            if ($row_total = $result_total->fetch_assoc()) {
                $total = $row_total['total_bills'];
            }
            $result_total->free();
        }
    }

    ob_clean();
    echo json_encode([
        "total_bills" => $total,
        "bills" => $bills
    ]);

} else {
    ob_clean();
    echo json_encode(["error" => "Error executing procedure: " . $conn->error]);
}

$conn->close();
?>
