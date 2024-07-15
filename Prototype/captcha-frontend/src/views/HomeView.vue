<template>
  <div class="homeView">
    <h1>HomeView</h1>

    <router-link :to="{name:'pair'}">See Pairs</router-link>
    <p></p>
    <router-link :to="{name:'list-challenge'}">See list challenge</router-link>
  </div>
</template>

<script>
// @ is an alias to /src


export default {
  name: "HomeView"
};
</script>
