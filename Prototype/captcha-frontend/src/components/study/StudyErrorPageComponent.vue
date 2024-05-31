<template>
  <div class="StudyErrorPageComponent">
    <p>There was an error with saving your Feedback. I would appreciate if you try again later</p>
    <router-link :to="{name:'study'}">Go back to Start</router-link>
  </div>
</template>

<script>
//TODO: when click on go back to start, the study should be reset 
export default {
  name: "StudyErrorPage"
};
</script>

<style scoped>

</style>