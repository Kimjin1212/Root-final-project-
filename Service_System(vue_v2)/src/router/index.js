import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/photo/photo.vue";
import LogListView from "../views/log/loglist.vue";
import LoginView from "../views/login/LoginView.vue";
import RegisterView from "../views/login/RegisterView.vue";
const routers = [
  {
    path: "/",
    name: "home",
    component: LoginView,
  },
  { path: "/login", component: LoginView },
  { path: "/register", component: RegisterView },
  {
    path: "/home",
    component: HomeView,
    meta: { requiresAuth: true },
  },
  {
    path: "/logs",
    component: LogListView,
    meta: { requiresAuth: true },
  },
];
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routers,
});

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem("isLoggedIn") || false;
  clearInterval(localStorage.getItem("interval_id"));
  if (
    to.matched.some((record) => record.meta.requiresAuth) &&
    !isAuthenticated
  ) {
    next("/login");
  } else {
    next();
  }
});

export default router;
