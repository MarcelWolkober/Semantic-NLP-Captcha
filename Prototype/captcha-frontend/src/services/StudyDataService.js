import http from "../http-common";


class StudyDataService {
  getNewStudy() {
    return http.get("/study/new");

  }

  getAll() {
    return http.get("/study/all");
  }

  postStudy(results) {
    return http.post("/study/add", results);
  }
}
export default new StudyDataService();