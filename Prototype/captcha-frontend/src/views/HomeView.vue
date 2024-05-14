<template>
  <div class="homeView">
    <h1>HomeView</h1>
    <p>You are about to see pairs:</p>
    <router-link :to="{name:'pair'}">See Pairs</router-link>
  </div>
</template>

<script>
// @ is an alias to /src


export default {
  name: "HomeView"
};
</script>
