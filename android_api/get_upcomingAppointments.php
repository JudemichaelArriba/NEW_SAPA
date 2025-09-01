<?php
header("Content-Type: application/json; charset=utf-8");
require_once 'db_config.php';

$user_id = isset($_GET['user_id']) ? $_GET['user_id'] : '';

if (empty($user_id)) {
    echo json_encode(['error' => 'User ID is required']);
    exit;
}

$appointments = [];

try {
    $stmt = $conn->prepare("CALL GetUpcomingAppointments(?)");
    $stmt->bind_param("s", $user_id);
    $stmt->execute();

    $result = $stmt->get_result();
    while ($row = $result->fetch_assoc()) {
        $appointments[] = [
            'appointment_id'    => $row['appointment_id'],
            'slot_id'           => $row['slot_id'],
            'appointment_status'=> $row['appointment_status'],
            'created_at'        => $row['created_at'],
            'slot_name'         => $row['slot_name'],
            'start_time'        => $row['start_time'],
            'end_time'          => $row['end_time'],
            'hospital_id'       => $row['hospital_id'],
            'hospital_name'     => $row['hospital_name'],
            'section_id'        => $row['section_id'],
            'section_name'      => $row['section_name'],
            'student_ids'       => $row['student_ids'],
            'student_count'     => $row['student_count']
        ];
    }

    echo json_encode($appointments, JSON_UNESCAPED_UNICODE);

    $stmt->close();
    $conn->next_result(); 

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => $e->getMessage()]);
}

$conn->close();
exit;
