<?php
$host = "localhost";
$user = "SC_Coordinator1";
$pass = "bg]OLq1*(R.]IQMh";
$db = "sapa5";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>