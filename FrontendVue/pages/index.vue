<template>
  <div>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
      <h1 class="mx-auto" style="text-align: center; color: white;">Welcome to Occurrences Management</h1>
    </nav>
    <b-container class="mx-auto" style="text-align: center; margin-top: 50px;">
      <h3>To use our platform to manage your Occurrences,
        you have to
        Login.</h3>
      <button type="button" class="btn btn-dark" @click="redirectToLogin" style="margin-top:30px">
        Login Into Our Site
      </button>
    </b-container>
  </div>

</template>
<script>
export default {
  methods: {
    redirectToLogin() {
      this.$router.push("/auth/login");
    }
  }
};
</script>