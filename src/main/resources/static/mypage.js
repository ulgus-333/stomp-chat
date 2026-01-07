
document.addEventListener('DOMContentLoaded', function () {
    const nicknameInput = document.getElementById('nickname');
    const updateNicknameButton = document.getElementById('update-nickname');

    // Fetch current user's nickname and display it
    fetch('/user')
        .then(response => response.json())
        .then(data => {
            nicknameInput.value = data.nickName;
        })
        .catch(error => console.error('Error fetching user data:', error));

    // Handle nickname update
    updateNicknameButton.addEventListener('click', function () {
        const newNickname = nicknameInput.value;
        if (newNickname) {
            fetch('/user/profile', {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ nickname: newNickname }),
            })
            .then(response => {
                if (response.ok) {
                    alert('Nickname updated successfully!');
                    window.location.href = "/index.html";
                } else {
                    alert('Failed to update nickname.');
                }
            })
            .catch(error => console.error('Error updating nickname:', error));
        }
    });
});
