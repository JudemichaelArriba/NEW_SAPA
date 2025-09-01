<?php
header("Content-Type: application/json");

require_once 'db_config.php';

$hospitals = [];

$stmt = $conn->prepare("CALL getHospitals()");
$stmt->execute();

$result = $stmt->get_result();
while ($row = $result->fetch_assoc()) {
    $hospitals[] = [
        'hospital_id' => $row['hospital_id'],
        'hospital_name' => $row['hospital_name'],
        'hospital_address' => $row['hospital_address'],
        'hospital_email' => $row['hospital_email'],
        'hospital_contact' => $row['hospital_contact'],
        'date_added' => $row['date_added']
    ];
}

echo json_encode($hospitals, JSON_UNESCAPED_SLASHES);

$stmt->close();
$conn->close();
?>
