import http from "../http-common";


class StudyDataService {
  getAll() {
    return http.get("/study/all");
  }

  postStudy(results) {
    return http.post("/study/add", results);
  }
}
export default new StudyDataService();