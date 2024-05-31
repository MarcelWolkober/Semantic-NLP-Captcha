<template>
  <div class="StudyFinishedComponent">

    <p>Study finished successfully. You can close this page now</p>
    <router-link :to="{name:'study'}">Go Home</router-link>
  </div>
</template>

<script>
export default {
  name: "StudyFinishedComponent"
};
</script>

<style scoped>

</style>