<?php
// delete_student.php

header('Content-Type: application/json');

if (!isset($_GET['student_id'])) {
    echo json_encode([
        'success' => false,
        'message' => 'Missing student_id parameter'
    ]);
    exit;
}

$student_id = $_GET['student_id'];


require_once 'db_config.php';

try {
    
    $stmt = $conn->prepare("CALL deleteStudentById(?)");
    if (!$stmt) {
        throw new Exception($conn->error);
    }

    $stmt->bind_param("s", $student_id);
    $stmt->execute();

    echo json_encode([
        'status' => 'success',
        'message' => 'Student deleted successfully'
    ]);

    $stmt->close();
    $conn->close();

} catch (mysqli_sql_exception $e) {
   
    echo json_encode([
        'status' => false,
        'message' => $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'status' => false,
        'message' => $e->getMessage()
    ]);
}
?>
