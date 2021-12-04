<?php
$salt_length = 32;

function userExists($username){
	$query = "SELECT username FROM members WHERE username = ?";
	global $con;
	if($stmt = $con->prepare($query)){
		$stmt->bind_param("s",$username);
		$stmt->execute();
		$stmt->store_result();
		$stmt->fetch();
		if($stmt->num_rows == 1){
			$stmt->close();
			return true;
		}
		$stmt->close();
	}
 
	return false;
}
 
// Create a Salt for hashing the password

function getSalt(){
	global $salt_length;
	return bin2hex(openssl_random_pseudo_bytes($salt_length));
}
 

// Creates password hash using the Salt and the password

function concatPasswordWithSalt($password,$salt){
	global $random_salt_length;
	if($random_salt_length % 2 == 0){
		$mid = $salt_length / 2;
	}
	else{
		$mid = ($salt_length - 1) / 2;
	}
 
	return
	substr($salt,0,$mid - 1).$password.substr($salt,$mid,$salt_length - 1);
 
}
?>