import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import PairView from "@/views/PairView.vue";
import NotFound from "@/components/NotFound.vue";
import PairChallengeView from "@/views/PairChallengeView.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView
  },
  {
    path: "/pair",
    name: "pair",
    component: PairView

  },
  {
    path: "/pair-challenge",
    name: "pair-challenge",
    component: PairChallengeView
  },

  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ "../views/AboutView.vue")
  },
  //catch all 404
  {
    path: "/:catchAll(.*)",
    name: "404",
    component: NotFound
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;