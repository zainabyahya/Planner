<?php
define('DB_USER', "lr_backend"); 
define('DB_PASSWORD', "-?VEW+}Nht,#"); 
define('DB_DATABASE', "login_register_backend"); 
define('DB_SERVER', "localhost"); 
 
$con = mysqli_connect(DB_SERVER,DB_USER,DB_PASSWORD,DB_DATABASE);
 
// Check connection
if(mysqli_connect_errno())
{
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
} 
?>