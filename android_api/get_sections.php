<?php
header("Content-Type: application/json");
require_once 'db_config.php';

$hospital_id = isset($_GET['hospital_id']) ? $_GET['hospital_id'] : '';

if(empty($hospital_id)){
    echo json_encode(['error' => 'Hospital ID is required']);
    exit;
}

$sections = [];

$stmt = $conn->prepare("CALL GetHospitalSectionsWithHospitalName(?)");
$stmt->bind_param("s", $hospital_id);
$stmt->execute();

$result = $stmt->get_result();
while($row = $result->fetch_assoc()) {
    $sections[] = [
        'section_id' => $row['section_id'],
        'section_name' => $row['section_name'],
        'section_description' => $row['section_description'],
        'hospital_id' => $row['hospital_id'],
        'hospital_name' => $row['hospital_name'],
        'billing' => $row['billing']
    ];
}

echo json_encode($sections, JSON_UNESCAPED_SLASHES);

$stmt->close();
$conn->close();
?>
