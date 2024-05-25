<template>
  <div id="nav">
    <router-link to="/">Home</router-link>    |
    <router-link to="/about">About</router-link>
  </div>
  <img alt="Vue logo" src="./assets/logo.png" />
  <router-view />
</template>

<script>
import PairComponent from "@/components/challenges/PairComponent.vue";

export default {
  name: "App",
  data() {
    return {
      msg: "Title"
    };
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
