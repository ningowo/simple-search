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

export function showFolder(userId) {
  return request({
    url: API_BASE_URL + "/api/collect/show?userId=" + userId,
    method: "POST",
  });
}

export function addFolder(userId, favouriteName) {
  return request({
    url:
      API_BASE_URL +
      "/api/collect/add?userId=" +
      userId +
      "&favouriteName=" +
      favouriteName,
    method: "POST",
  });
}

export function renameFolder(userId,originFavouriteName,newFavouriteName) {
  return request({
    url: API_BASE_URL + "/api/collect/rename?userId=" +
    userId +
    "&originFavouriteName=" +
    originFavouriteName +
    "&newFavouriteName=" +
    newFavouriteName,
    method: "POST",
  });
}
export function delFolder(userId,favouriteName) {
  return request({
    url: API_BASE_URL + "/api/collect/delete?userId=" +
    userId +
    "&favouriteName=" +
    favouriteName,
    method: "POST",
  });
}

export function showFav(favouriteId) {
  return request({
    url: API_BASE_URL + "/api/collect/article/show?favouriteId=" + favouriteId,
    method: "POST",
  });
}
export function addFav(favouriteId, dataId) {
  return request({
    url: API_BASE_URL + "/api/collect/article/add?favouriteId=" +
    favouriteId +
    "&dataId=" +
    dataId,
    method: "POST",
  });
}
export function delFav(favouriteId, dataId) {
  return request({
    url:
      API_BASE_URL +
      "/api/collect/article/delete?favouriteId=" +
      favouriteId +
      "&dataId=" +
      dataId,
    method: "POST",
  });
}
