<?php
$response = array();
include 'db_connect.php';
include 'functions.php';

//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE); //convert JSON into array
 
//Check for Mandatory parameters
if(isset($input['username']) && isset($input['password']) && isset($input['full_name'])){
	$username = $input['username'];
	$password = $input['password'];
	$fullName = $input['full_name'];
	
	//Check if user already exist
	if(!userExists($username)){
 
		//Get a unique Salt (to get more secure password)
		$salt         = getSalt();
		
		//Generate a unique password Hash
		$passwordHash = password_hash(concatPasswordWithSalt($password,$salt),PASSWORD_DEFAULT);
		
		//Query to register new user and put it in the database 
		$insertQuery  = "INSERT INTO members(username, full_name, password_hash, salt) VALUES (?,?,?,?)";
		if($stmt = $con->prepare($insertQuery)){
			$stmt->bind_param("ssss",$username,$fullName,$passwordHash,$salt);
			$stmt->execute();
			$response["status"] = 0;
			$response["message"] = "User created successfully.";
			$stmt->close();
		}
	}
	else{
		$response["status"] = 1;
		$response["message"] = "User exists";
	}
}
else{
	$response["status"] = 2;
	$response["message"] = "Missing mandatory parameters";
}
echo json_encode($response);
?>