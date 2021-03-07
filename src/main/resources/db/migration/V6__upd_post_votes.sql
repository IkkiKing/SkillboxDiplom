update post_votes pv
  set pv.value = -1
where pv.value = 0;
