document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('form[data-validate-form]').forEach((form) => {
    form.setAttribute('novalidate', 'novalidate');

    form.addEventListener('submit', (event) => {
      clearErrors(form);

      const fields = Array.from(form.querySelectorAll('[data-required], [data-email], [data-min-length], [data-min-value], [data-number], [data-match]'));
      const firstInvalid = fields.find((field) => {
        const message = validateField(field, form);
        if (message) {
          showFieldError(field, message);
          return true;
        }
        return false;
      });

      if (firstInvalid) {
        event.preventDefault();
        if (firstInvalid.type !== 'hidden') {
          firstInvalid.focus();
        }
      }
    });
  });
});

function validateField(field, form) {
  const value = field.value.trim();
  const label = field.dataset.label || 'Trường này';

  if (field.dataset.required !== undefined && value === '') {
    return field.dataset.requiredMessage || `${label} không được để trống.`;
  }

  if (value === '') {
    return '';
  }

  if (field.dataset.email !== undefined && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    return field.dataset.emailMessage || 'Email không đúng định dạng.';
  }

  if (field.dataset.number !== undefined && !/^\d+(\.\d+)?$/.test(value)) {
    return field.dataset.numberMessage || `${label} phải là số hợp lệ.`;
  }

  if (field.dataset.minLength && value.length < Number(field.dataset.minLength)) {
    return field.dataset.minLengthMessage || `${label} phải có ít nhất ${field.dataset.minLength} ký tự.`;
  }

  if (field.dataset.minValue && Number(value) < Number(field.dataset.minValue)) {
    return field.dataset.minValueMessage || `${label} phải lớn hơn hoặc bằng ${field.dataset.minValue}.`;
  }

  if (field.dataset.match) {
    const matchedField = form.querySelector(`[name="${field.dataset.match}"]`);
    if (matchedField && value !== matchedField.value.trim()) {
      return field.dataset.matchMessage || `${label} không khớp.`;
    }
  }

  return '';
}

function showFieldError(field, message) {
  const group = field.dataset.errorTarget
    ? document.querySelector(field.dataset.errorTarget)
    : field.closest('.form-group') || field.parentElement;
  const error = document.createElement('div');
  error.className = 'field-error';
  error.textContent = message;
  group.appendChild(error);
  field.classList.add('form-control-error');
  field.setAttribute('aria-invalid', 'true');
}

function clearErrors(form) {
  form.querySelectorAll('.field-error').forEach((error) => error.remove());
  form.querySelectorAll('.form-control-error').forEach((field) => {
    field.classList.remove('form-control-error');
    field.removeAttribute('aria-invalid');
  });
}
