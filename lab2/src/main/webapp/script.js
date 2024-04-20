(() => {
    const dateHTML = document.querySelector('#dateHTML');
    const timeHTML = document.querySelector('#timeHTML');
    const additionHTML = document.querySelector('#additionHTML');
    const additionFormHTML = document.querySelector('#additionFormHTML');
    const additionXHTML = document.querySelector('#additionXHTML');
    const additionYHTML = document.querySelector('#additionYHTML');
    const divisionHTML = document.querySelector('#divisionHTML');
    const divisionFormHTML = document.querySelector('#divisionFormHTML');
    const divisionXHTML = document.querySelector('#divisionXHTML');
    const divisionYHTML = document.querySelector('#divisionYHTML');
    const quadraticHTML = document.querySelector('#quadraticHTML');
    const quadraticFormHTML = document.querySelector('#quadraticFormHTML');
    const quadraticAHTML = document.querySelector('#quadraticAHTML');
    const quadraticBHTML = document.querySelector('#quadraticBHTML');
    const quadraticCHTML = document.querySelector('#quadraticCHTML');
    const tableHTML = document.querySelector('#tableHTML');
    const tableBodyHTML = tableHTML?.getElementsByTagName('tbody')[0];
    const calculateHTML = document.querySelector('#calculateHTML');
    const clearHTML = document.querySelector('#clearHTML');

    setInterval(() => {
        dateHTML.innerHTML = new Date().toLocaleString('pl-PL', {
            day: 'numeric',
            month: 'long',
            year: 'numeric',
        });
        timeHTML.innerHTML = new Date().toLocaleTimeString('pl-PL');
    }, 1);

    additionHTML.addEventListener('click', () => {
        additionFormHTML.style.display = 'block';
        divisionFormHTML.style.display = 'none';
        quadraticFormHTML.style.display = 'none';
    });

    divisionHTML.addEventListener('click', () => {
        additionFormHTML.style.display = 'none';
        divisionFormHTML.style.display = 'block';
        quadraticFormHTML.style.display = 'none';
    });

    quadraticHTML.addEventListener('click', () => {
        additionFormHTML.style.display = 'none';
        divisionFormHTML.style.display = 'none';
        quadraticFormHTML.style.display = 'block';
    });

    calculateHTML.addEventListener('click', () => {
        const trHTML = document.createElement('tr');
        const tdIdHTML = document.createElement('td');
        const tdOperationHTML = document.createElement('td');
        const tdResultHTML = document.createElement('td');

        trHTML.append(tdIdHTML, tdOperationHTML, tdResultHTML);
        tableBodyHTML.prepend(trHTML);
        tdIdHTML.innerHTML = Number(tableBodyHTML.children.length + 1);
        tdResultHTML.colSpan = 2;

        if (additionFormHTML.style.display === 'block') {
            tdOperationHTML.innerHTML = `${additionXHTML.value} + ${additionYHTML.value} = x`;
            tdResultHTML.innerHTML = additionXHTML.value && additionYHTML.value
                ? Number(additionXHTML.value) + Number(additionYHTML.value)
                : 'ERROR (empty input)';
        } else if (divisionFormHTML.style.display === 'block') {
            tdOperationHTML.innerHTML = `${divisionXHTML.value} / ${divisionYHTML.value} = x`;

            if (!divisionXHTML.value || !divisionYHTML.value) {
                tdResultHTML.innerHTML = 'ERROR (empty input)';
            } else if (Number(divisionYHTML.value) === 0) {
                tdResultHTML.innerHTML = 'ERROR (division by zero)';
            } else {
                tdResultHTML.innerHTML = Number(divisionXHTML.value) / Number(divisionYHTML.value);
            }
        } else if (quadraticFormHTML.style.display === 'block') {
            tdOperationHTML.innerHTML = `${quadraticAHTML.value}x^2 + ${quadraticBHTML.value}x + ${quadraticCHTML.value} = 0`;

            if (!quadraticAHTML.value || !quadraticBHTML.value || !quadraticCHTML.value) {
                tdResultHTML.innerHTML = 'ERROR (empty input)';
            } else {
                const a = Number(quadraticAHTML.value);
                const b = Number(quadraticBHTML.value);
                const c = Number(quadraticCHTML.value);
                const delta = b ** 2 - 4 * a * c;

                if (delta > 0) {
                    const x1 = (-b - Math.sqrt(delta)) / (2 * a);
                    const x2 = (-b + Math.sqrt(delta)) / (2 * a);

                    const tdResult1HTML = tdResultHTML;
                    const tdResult2HTML = document.createElement('td');

                    tdResult1HTML.colSpan = 1;
                    tdResult2HTML.colSpan = 1;

                    tdResult1HTML.innerHTML = x1;
                    tdResult2HTML.innerHTML = x2;

                    trHTML.append(tdResult2HTML);
                } else if (delta === 0) {
                    const x = -b / (2 * a);
                    tdResultHTML.innerHTML = `x = ${x}`;
                } else {
                    tdResultHTML.innerHTML = 'no real roots';
                }
            }
        } else {
            tdOperationHTML.innerHTML = '---';
            tdResultHTML.innerHTML = 'ERROR';
        }
    });

    clearHTML.addEventListener('click', () => {
        tableBodyHTML.replaceChildren()
    });
})();
