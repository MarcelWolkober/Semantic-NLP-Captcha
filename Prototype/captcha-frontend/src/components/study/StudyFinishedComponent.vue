<template>
  <div class="StudyFinishedComponent">

    <p>Study finished successfully.</p>
    <router-link :to="{name:'home'}">Go Home</router-link>
  </div>
</template>

<script>
export default {
  name: "StudyFinishedComponent"
};
</script>

<style scoped>

</style>