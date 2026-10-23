/* =====================================================================
   Automated Scholarship Discovery and Application Tracker
   Client-side JavaScript — light UX helpers only.
   All authoritative validation happens server-side in the servlets;
   this file just gives the user faster feedback and small interactions.
   ===================================================================== */

document.addEventListener('DOMContentLoaded', function () {
    initDeleteConfirmModal();
    initClientSideValidation();
    initAutoDismissAlerts();
    initClearFilters();
});

/* ---------------------------------------------------------------------
   Delete-scholarship confirmation modal (used on admin/scholarships.jsp)
   --------------------------------------------------------------------- */
function initDeleteConfirmModal() {
    const overlay = document.getElementById('deleteModalOverlay');
    if (!overlay) return;

    const nameSpan = document.getElementById('deleteModalScholarshipName');
    const form = document.getElementById('deleteModalForm');
    const idInput = document.getElementById('deleteModalScholarshipId');
    const cancelBtn = document.getElementById('deleteModalCancel');

    document.querySelectorAll('.js-delete-trigger').forEach(function (btn) {
        btn.addEventListener('click', function () {
            const id = btn.getAttribute('data-id');
            const name = btn.getAttribute('data-name');
            idInput.value = id;
            nameSpan.textContent = name;
            overlay.classList.add('open');
        });
    });

    cancelBtn.addEventListener('click', function () {
        overlay.classList.remove('open');
    });

    overlay.addEventListener('click', function (e) {
        if (e.target === overlay) overlay.classList.remove('open');
    });
}

/* ---------------------------------------------------------------------
   Basic client-side validation for register/profile/add-scholarship
   forms — mirrors (not replaces) the server-side rules.
   --------------------------------------------------------------------- */
function initClientSideValidation() {
    document.querySelectorAll('form.js-validate').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let valid = true;
            form.querySelectorAll('[data-required="true"]').forEach(function (field) {
                clearFieldError(field);
                if (!field.value || field.value.trim() === '') {
                    valid = false;
                    showFieldError(field, 'This field is required.');
                }
            });

            form.querySelectorAll('[data-min="0"][data-max]').forEach(function (field) {
                if (field.value === '') return;
                const val = parseFloat(field.value);
                const max = parseFloat(field.getAttribute('data-max'));
                if (isNaN(val) || val < 0 || val > max) {
                    valid = false;
                    showFieldError(field, 'Enter a value between 0 and ' + max + '.');
                }
            });

            const email = form.querySelector('input[type="email"]');
            if (email && email.value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
                valid = false;
                showFieldError(email, 'Enter a valid email address.');
            }

            if (!valid) e.preventDefault();
        });
    });
}

function showFieldError(field, message) {
    field.style.borderColor = '#c0392b';
    let err = field.parentElement.querySelector('.field-error');
    if (!err) {
        err = document.createElement('div');
        err.className = 'field-error';
        err.style.color = '#c0392b';
        err.style.fontSize = '12.5px';
        err.style.marginTop = '4px';
        field.parentElement.appendChild(err);
    }
    err.textContent = message;
}

function clearFieldError(field) {
    field.style.borderColor = '';
    const err = field.parentElement.querySelector('.field-error');
    if (err) err.remove();
}

/* ---------------------------------------------------------------------
   Auto-dismiss success/info alerts after a few seconds
   --------------------------------------------------------------------- */
function initAutoDismissAlerts() {
    document.querySelectorAll('.alert-success, .alert-info').forEach(function (alertBox) {
        setTimeout(function () {
            alertBox.style.transition = 'opacity .4s ease';
            alertBox.style.opacity = '0';
            setTimeout(function () { alertBox.style.display = 'none'; }, 400);
        }, 5000);
    });
}

/* ---------------------------------------------------------------------
   "Clear Filters" button on the scholarship list page
   --------------------------------------------------------------------- */
function initClearFilters() {
    const clearBtn = document.getElementById('clearFiltersBtn');
    if (!clearBtn) return;
    clearBtn.addEventListener('click', function (e) {
        e.preventDefault();
        window.location.href = clearBtn.getAttribute('data-base-url');
    });
}

/* ---------------------------------------------------------------------
   Confirms before submitting the "Mark as Applied" form, reminding the
   student this should only be done after actually applying externally.
   --------------------------------------------------------------------- */
function confirmMarkAsApplied() {
    return confirm('Have you completed and submitted your application on the official scholarship website? ' +
        'Click OK only after you have finished applying there.');
}
