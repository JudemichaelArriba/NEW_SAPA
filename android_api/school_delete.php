<?php


header('Content-Type: application/json');


if (!isset($_GET['school_id'])) {
    echo json_encode([
        'success' => false,
        'message' => 'Missing school_id parameter'
    ]);
    exit;
}

$school_id = $_GET['school_id'];


require_once 'db_config.php';

try {
    
    $stmt = $conn->prepare("CALL deleteSchoolById(?)");
    if (!$stmt) {
        throw new Exception($conn->error);
    }

    $stmt->bind_param("s", $school_id);
    $stmt->execute();

    echo json_encode([
        'success' => 'success',
        'message' => 'School deleted successfully'
    ]);

    $stmt->close();
    $conn->close();

} catch (mysqli_sql_exception $e) {
    echo json_encode([
        'success' => 'success',
        'message' => $e->getMessage()
    ]);
} catch (Exception $e) {
    echo json_encode([
        'success' => 'success',
        'message' => $e->getMessage()
    ]);
}
?>
