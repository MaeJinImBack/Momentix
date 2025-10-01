-- KEYS[1]: 잠금을 해제할 키
-- ARGV[1]: 잠금을 소유한 클라이언트의 고유 ID

-- 현재 키의 소유자가 나 자신인지 확인한다.
if redis.call('GET', KEYS[1]) == ARGV[1] then
  -- 소유자가 맞으면 키를 삭제하고 1을 반환한다.
  return redis.call('DEL', KEYS[1])
else
  -- 소유자가 아니면 아무것도 하지 않고 0을 반환한다.
  return 0
end