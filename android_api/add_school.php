<?php
require 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $required = ['school_name', 'school_address', 'school_email', 'contact_no', 'coordinator_id'];
    $missing = array_diff($required, array_keys($_POST));

    if (!empty($missing) || !isset($_FILES['profile_image'])) {
        echo json_encode([
            "status" => "error",
            "message" => "Missing fields: " . implode(', ', $missing)
        ]);
        exit;
    }

    $image = $_FILES['profile_image'];
    if ($image['error'] !== UPLOAD_ERR_OK) {
        echo json_encode(["status" => "error", "message" => "Image upload error"]);
        exit;
    }

    $imageBlob = file_get_contents($image['tmp_name']);

 
    if (substr($imageBlob, 0, 2) !== "\xFF\xD8" || substr($imageBlob, -2) !== "\xFF\xD9") {
        echo json_encode([
            "status" => "error",
            "message" => "Invalid JPEG image (corrupted header/footer)"
        ]);
        exit;
    }

    try {
        $stmt = $conn->prepare("CALL add_school(?, ?, ?, ?, ?, ?)");
        if (!$stmt) throw new Exception($conn->error);

        $null = null;
        $stmt->bind_param("sssssb",
            $_POST['school_name'],
            $_POST['school_address'],
            $_POST['school_email'],
            $_POST['contact_no'],
            $_POST['coordinator_id'],
            $null
        );

        $stmt->send_long_data(5, $imageBlob);

        if ($stmt->execute()) {
            echo json_encode([
                "status" => "success",
                "message" => "School added successfully"
            ]);
        } else {
          
            $errorMessage = $stmt->error ?: $conn->error;
            echo json_encode([
                "status" => "error",
                "message" => $errorMessage
            ]);
        }

    } catch (mysqli_sql_exception $e) {
       
        echo json_encode([
            "status" => "error",
            "message" => $e->getMessage()
        ]);
    } finally {
        if (isset($stmt)) $stmt->close();
        $conn->close();
    }

} else {
    echo json_encode([
        "status" => "error",
        "message" => "Only POST method is allowed"
    ]);
}
?>
