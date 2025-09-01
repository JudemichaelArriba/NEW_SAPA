<?php
header("Content-Type: application/json");
require_once 'db_config.php';

$appointment_id = $_GET['appointment_id'] ?? null;
$students = [];

if ($appointment_id) {
    if ($stmt = $conn->prepare("CALL get_students_by_appointment_id(?)")) {
        $stmt->bind_param("i", $appointment_id);
        $stmt->execute();

       
        $result = $stmt->get_result();
        if ($result) {
            while ($row = $result->fetch_assoc()) {
                $students[] = [
                    'student_id'       => $row['student_id'],
                    'student_fullname' => $row['student_fullname'],
                    'email'            => $row['email'],
                    'contact_no'       => $row['contact_no'],
                    'birthdate'        => $row['birthdate'],
                    'gender'           => $row['gender'],
                    'coordinator_id'   => $row['coordinator_id'],
                    'school_id'        => $row['school_id'],
                    'school_name'      => $row['school_name']
                ];
            }
            $result->free();
        }

        $stmt->close();
      
        while ($conn->more_results() && $conn->next_result()) {;}
    }
}

echo json_encode($students, JSON_UNESCAPED_SLASHES);

$conn->close();
?>
