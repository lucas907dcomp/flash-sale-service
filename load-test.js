import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '5s', target: 500 },
        { duration: '10s', target: 500 },
        { duration: '5s', target: 0 },
    ],
    thresholds: {

        http_req_duration: ['p(95)<500'],
    },
};

export default function () {

    const url = 'http://host.docker.internal:8080/api/purchases';

    const payload = JSON.stringify({
        productId: 1,
        quantity: 1,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 200 (Comprou)': (r) => r.status === 200,
        'status is 409 (Sem Estoque/Lock)': (r) => r.status === 409,
        'status is 500 (Erro Crítico)': (r) => r.status === 500,
    });

    sleep(0.1);
}