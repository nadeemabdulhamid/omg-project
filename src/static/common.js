/* 
    OMG Project
    Copyright (c) 2025 Nadeem Abdul Hamid
    License: MIT
 */


/**
 * Fetches JSON data from a URL and returns it as a JSON object.
 * 
 * @param {String} url the URL string
 * @param {Object} params a JSON object that will be converted to URL parameters
 * @returns a JSON object or null if there was an error
 */    
async function fetchJSON(url, params = {}) {
    try {
        const response = await fetch(url + "?" + new URLSearchParams(params).toString());
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error(error.message);
    }    
}
