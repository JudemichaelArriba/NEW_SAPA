<?php
header("Content-Type: application/json");
require_once 'db_config.php';


$section_id = isset($_GET['section_id']) ? $_GET['section_id'] : '';

if(empty($section_id)){
    echo json_encode(['error' => 'Section ID is required']);
    exit;
}

$slots = [];

$stmt = $conn->prepare("CALL GetSlotsBySectionId(?)");
$stmt->bind_param("i", $section_id);
$stmt->execute();

$result = $stmt->get_result();
while($row = $result->fetch_assoc()) {
    $slots[] = [
        'slot_id' => $row['slot_id'],
        'slot_name' => $row['slot_name'],
        'start_time' => $row['start_time'],
        'end_time' => $row['end_time'],
        'max_capacity' => $row['max_capacity'],
        'hospital_id' => $row['hospital_id'],
        'hospital_name' => $row['hospital_name'],
        'section_id' => $row['section_id'],
        'section_name' => $row['section_name']
    ];
}

echo json_encode($slots, JSON_UNESCAPED_SLASHES);

$stmt->close();
$conn->close();
?>
