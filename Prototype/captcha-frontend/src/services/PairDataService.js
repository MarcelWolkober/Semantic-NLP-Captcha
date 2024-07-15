import http from "../http-common";

class PairDataService {
  getAll() {
    return http.get("/usagepairs/all");
  }

  get(id) {
    return http.get(`/tutorials/${id}`);
  }
  getNext(){
    return http.get("/usagepairs/next");
  }
  postChoice(data){
    return http.post("/resolve/pair",data);
  }
}
export default new PairDataService();