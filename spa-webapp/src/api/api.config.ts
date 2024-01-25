import axios from 'axios';

export const api = axios.create({
    baseURL: 'http://localhost:8000/api/v1/',
    headers: {
        'Content-Type': 'application/json',
    }
})

// api.interceptors.response.use(
//     function (response) {
//         return response;
//     },
//     function (error) {
//         console.log(error)
//     }
// )