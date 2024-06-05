import axios from "axios";

export default axios.create({
  baseURL: "http://back-end:8081/api",
  headers: {
    "Content-type": "application/json",

  }
});