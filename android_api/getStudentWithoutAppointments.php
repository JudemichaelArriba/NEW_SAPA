<?php
header("Content-Type: application/json");
require_once 'db_config.php';

$coordinator_id = $_GET['coordinator_id'] ?? null;
$school_id = $_GET['school_id'] ?? null;
$students = [];

if ($coordinator_id && $school_id) {
    $stmt = $conn->prepare("CALL getStudentWithoutAppointments(?, ?)");
    $stmt->bind_param("ss", $coordinator_id, $school_id); 
    $stmt->execute();

    $result = $stmt->get_result();
    if ($result) {
        while ($row = $result->fetch_assoc()) {
            $students[] = [
                'student_id' => $row['student_id'],
                'student_fullname' => $row['student_fullname'],
                'email' => $row['email'],
                'contact_no' => $row['contact_no'],
                'birthdate' => $row['birthdate'],
                'gender' => $row['gender'],
                'coordinator_id' => $row['coordinator_id'],
                'school_id' => $row['school_id'],
                'school_name' => $row['school_name']
            ];
        }
        $result->free();
    }

    $stmt->close();
}

echo json_encode($students, JSON_UNESCAPED_SLASHES);

$conn->close();
?>
