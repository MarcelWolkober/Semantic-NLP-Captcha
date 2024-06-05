import axios from "axios";

export default axios.create({
  baseURL: "http://captcha-back-end:8081/api",
  headers: {
    "Content-type": "application/json",

  }
});