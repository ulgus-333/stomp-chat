document.addEventListener('DOMContentLoaded', function () {
    const nicknameInput = document.getElementById('nickname');
    const updateProfileButton = document.getElementById('update-profile');
    const profileImagePreview = document.getElementById('profile-image-preview');
    const profileImageUpload = document.getElementById('profile-image-upload');

    let profileImagePath = '';

    // Fetch current user's data and display it
    fetch('/user')
        .then(response => response.json())
        .then(data => {
            nicknameInput.value = data.nickName;
            if (data.profileImageUrl) {
                profileImagePreview.src = data.profileImageUrl;
                profileImagePath = data.profileImageUrl;
            } else {
                // You can set a default placeholder image if you have one
                profileImagePreview.src = 'https://via.placeholder.com/200';
            }
        })
        .catch(error => console.error('Error fetching user data:', error));

    // Handle file selection
    profileImageUpload.addEventListener('change', function () {
        const file = this.files[0];
        if (file) {
            uploadImageAndGetPath(file);
        }
    });

    // Upload image to OCI and get the path
    async function uploadImageAndGetPath(file) {
        // Step 1: Get a pre-signed URL from the server
        let presignedData;
        try {
            const response = await fetch('/files/presigned', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    pathType: 'PROFILE',
                    filename: file.name
                }),
            });
            if (!response.ok) {
                throw new Error('Failed to get a pre-signed URL.');
            }
            presignedData = await response.json();
        } catch (error) {
            console.error('Error getting pre-signed URL:', error);
            alert('Could not prepare image for upload.');
            return;
        }

        // Step 2: Upload the file to the pre-signed URL (OCI)
        try {
            const uploadResponse = await fetch(presignedData.parUrl, {
                method: 'PUT',
                body: file,
                headers: {
                    'Content-Type': file.type,
                }
            });

            if (!uploadResponse.ok) {
                throw new Error('File upload to OCI failed.');
            }
            
            // Step 3: Update UI and store the image path
            profileImagePreview.src = URL.createObjectURL(file); // Show a preview of the selected image
            profileImagePath = presignedData.fileName; // This is the path to be saved
            alert('Image ready to be saved with your profile.');

        } catch (error) {
            console.error('Error uploading file to OCI:', error);
            alert('Image upload failed.');
        }
    }


    // Handle profile update
    updateProfileButton.addEventListener('click', function () {
        const newNickname = nicknameInput.value;
        if (newNickname) {
            const payload = {
                nickname: newNickname,
                profileImageUrl: profileImagePath
            };

            fetch('/user/profile', {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            })
            .then(response => {
                if (response.ok) {
                    alert('Profile updated successfully!');
                    window.location.href = "/index.html";
                } else {
                    alert('Failed to update profile.');
                }
            })
            .catch(error => console.error('Error updating profile:', error));
        }
    });
});