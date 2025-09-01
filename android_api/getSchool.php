<?php
header("Content-Type: application/json");

require_once 'db_config.php';

$coordinator_id = $_GET['coordinator_id'];
$schools = [];

$stmt = $conn->prepare("CALL get_schools_by_coordinator(?)");
$stmt->bind_param("s", $coordinator_id);
$stmt->execute();

$result = $stmt->get_result();
while ($row = $result->fetch_assoc()) {
    $schools[] = [
        'school_id' => $row['school_id'],
        'school_name' => $row['school_name'],
        'school_address' => $row['school_address'],
        'school_email' => $row['school_email'],
        'contact_no' => $row['contact_no'],
        'request_status' => $row['request_status'],
        'coordinator_id' => $row['coordinator_id'],
        'approved_by' => $row['approved_by'],
        'requested_at' => $row['requested_at'],
        'approved_at' => $row['approved_at'],
        'profile_image' => !empty($row['profile_image']) ? base64_encode($row['profile_image']) : '',
        'student_count' => $row['student_count']
    ];
}

echo json_encode($schools, JSON_UNESCAPED_SLASHES);

$stmt->close();
$conn->close();
?>
