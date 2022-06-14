import { API_BASE_URL, ACCESS_TOKEN } from "../constants";

const request = (options) => {
  const headers = new Headers({
    "Content-Type": "application/json",
  });

  if (localStorage.getItem(ACCESS_TOKEN)) {
    headers.append(
      "Authorization",
      "Bearer " + localStorage.getItem(ACCESS_TOKEN)
    );
  }

  const defaults = { headers: headers };
  options = Object.assign({}, defaults, options);

  return fetch(options.url, options).then((response) =>
    response.json().then((json) => {
      if (!response.ok) {
        return Promise.reject(json);
      }
      return json;
    })
  );
};
export function search(query) {
  //47.104.4.41/search?query=重庆&filterWordList=&pageSize=10&pageNum=1
  return request({
    url:
      API_BASE_URL +
      "/search?query=" +
      query +
      "&filterWordList=&pageSize=10&pageNum=1",
    method: "GET",
  });
}
export function filterWord(query, filterWord, pageSize, pageNum) {
  console.log(filterWord);
  return request({
    url:
      API_BASE_URL +
      "/search?query=" +
      query +
      "&filterWordList=" +
      filterWord +
      "&pageSize=" +
      pageSize +
      "&pageNum=" +
      pageNum,
    method: "GET",
  });
}
export function login(loginRequest) {
  return request({
    url: API_BASE_URL + "/api/auth/signin",
    method: "POST",
    body: JSON.stringify(loginRequest),
  });
}

export function signup(signupRequest) {
  return request({
    url: API_BASE_URL + "/api/auth/signup",
    method: "POST",
    body: JSON.stringify(signupRequest),
  });
}

export function checkUsernameAvailability(username) {
  return request({
    url:
      API_BASE_URL + "/api/user/checkUsernameAvailability?username=" + username,
    method: "GET",
  });
}

export function checkEmailAvailability(email) {
  return request({
    url: API_BASE_URL + "/api/user/checkEmailAvailability?email=" + email,
    method: "GET",
  });
}

export function getCurrentUser() {
  if (!localStorage.getItem(ACCESS_TOKEN)) {
    return Promise.reject("No access token set.");
  }

  return request({
    url: API_BASE_URL + "/api/user/me",
    method: "GET",
  });
}

export function getUserProfile(username) {
  return request({
    url: API_BASE_URL + "/api/users/" + username,
    method: "GET",
  });
}
