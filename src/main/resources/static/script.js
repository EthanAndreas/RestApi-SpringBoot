const API_URL = 'http://localhost:8080/api/items';

let isEditing = false;

function formatPrice(price) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(price);
}

async function loadProducts() {
    try {
        updateApiStatus('loading');
        const response = await fetch(API_URL);
        const products = await response.json();
        displayProducts(products);
        updateApiStatus('connected');
    } catch (error) {
        console.error('Error loading products:', error);
        updateApiStatus('error', error.message);
    }
}

function displayProducts(products) {
    const tableBody = document.getElementById('productTableBody');
    tableBody.innerHTML = products.map(product => `
        <tr>
            <td>#${product.id}</td>
            <td>
                <strong>${product.name}</strong>
            </td>
            <td>${product.description}</td>
            <td>${formatPrice(product.price)}</td>
            <td>
                <button class="action-btn edit-btn" onclick="editProduct('${product.id}')">
                    <i class="fas fa-edit"></i> Edit
                </button>
                <button class="action-btn delete-btn" onclick="deleteProduct('${product.id}')">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </td>
        </tr>
    `).join('');
}

document.getElementById('productForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const productData = {
        name: document.getElementById('name').value,
        description: document.getElementById('description').value,
        price: parseFloat(document.getElementById('price').value)
    };

    if (isEditing) {
        const id = document.getElementById('productId').value;
        await updateProduct(id, productData);
    } else {
        await createProduct(productData);
    }

    resetForm();
    loadProducts();
});

async function createProduct(product) {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(product)
        });

        if (!response.ok) throw new Error('Error creating product');
        updateApiStatus('connected');
    } catch (error) {
        console.error('Error:', error);
        updateApiStatus('error', 'Error creating product');
    }
}

async function editProduct(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        const product = await response.json();

        document.getElementById('productId').value = product.id;
        document.getElementById('name').value = product.name;
        document.getElementById('description').value = product.description;
        document.getElementById('price').value = product.price;

        document.getElementById('formTitle').innerHTML = '<i class="fas fa-edit"></i> Edit Product';
        isEditing = true;

        document.querySelector('.form-container').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Error:', error);
        updateApiStatus('error', 'Error loading product details');
    }
}

async function updateProduct(id, product) {
    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(product)
        });

        if (!response.ok) throw new Error('Error updating product');
        updateApiStatus('connected');
    } catch (error) {
        console.error('Error:', error);
        updateApiStatus('error', 'Error updating product');
    }
}

async function deleteProduct(id) {
    if (!confirm('Are you sure you want to delete this product?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Error deleting product');

        loadProducts();
        updateApiStatus('connected');
    } catch (error) {
        console.error('Error:', error);
        updateApiStatus('error', 'Error deleting product');
    }
}

function resetForm() {
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('formTitle').innerHTML = '<i class="fas fa-plus-circle"></i> Add New Product';
    isEditing = false;
}

function updateApiStatus(status, message) {
    const statusElement = document.getElementById('apiStatus');
    const icon = statusElement.querySelector('i');
    const text = statusElement.querySelector('span');

    if (status === 'connected') {
        icon.style.color = '#4CAF50';
        text.textContent = 'API Connected';
    } else if (status === 'error') {
        icon.style.color = '#f44336';
        text.textContent = `API Error: ${message}`;
    } else {
        icon.style.color = '#FFC107';
        text.textContent = 'Connecting...';
    }
}

document.getElementById('searchInput').addEventListener('input', (e) => {
    const searchTerm = e.target.value.toLowerCase();
    const rows = document.querySelectorAll('#productTableBody tr');

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
});

document.querySelectorAll('.btn-primary, .btn-secondary, .btn-refresh').forEach(button => {
    button.addEventListener('mousedown', function() {
        this.style.transform = 'scale(0.98)';
    });

    button.addEventListener('mouseup', function() {
        this.style.transform = 'scale(1)';
    });

    button.addEventListener('mouseleave', function() {
        this.style.transform = 'scale(1)';
    });
});

document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
});