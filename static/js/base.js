const flashmsg = document.getElementById('flashmsg');
flashmsg.addEventListener('click', closeflash);

function closeflash(e) {
    flashmsg.style.display = 'none';
}