document.addEventListener('DOMContentLoaded', () => {
    const toast = document.querySelector('.toast');
    if (toast) {
        setTimeout(() => toast.remove(), 3200);
    }

    const dateInput = document.getElementById('appliedDate');
    if (dateInput && !dateInput.value) {
        dateInput.value = new Date().toISOString().split('T')[0];
    }
});
