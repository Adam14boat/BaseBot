function saveToStorage(key) {
    localStorage.setItem(key, JSON.stringify(parameters));
}

function getStorage(key) {
    return JSON.parse(localStorage.getItem(key));
}

function switchToStorage(key) {
    parameters = getStorage(key);
}

function deleteStorage(key) {
    localStorage.removeItem(key);
}

function getAllStorageKeys() {
    return Object.keys(localStorage);
}

function download(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}